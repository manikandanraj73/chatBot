#!/usr/bin/env pwsh
# Create a new feature
[CmdletBinding()]
param(
    [switch]$Json,
    [switch]$AllowExistingBranch,
    [switch]$DryRun,
    [string]$ShortName,
    [switch]$Help,
    [Parameter(Position = 0, ValueFromRemainingArguments = $true)]
    [string[]]$FeatureDescription
)
$ErrorActionPreference = 'Stop'
$maxBranchLength = 244

# Show help if requested
if ($Help) {
    Write-Host "Usage: ./create-new-feature.ps1 [-Json] [-DryRun] [-AllowExistingBranch] [-ShortName <name>] <feature description>"
    Write-Host ""
    Write-Host "Options:"
    Write-Host "  -Json               Output in JSON format"
    Write-Host "  -DryRun             Compute feature name and paths without creating directories or files"
    Write-Host "  -AllowExistingBranch  Reuse an existing feature directory if it already exists"
    Write-Host "  -ShortName <name>   Provide a custom short name (2-4 words) for the feature"
    Write-Host "  -Help               Show this help message"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  ./create-new-feature.ps1 'Add user authentication system' -ShortName 'user-auth'"
    Write-Host "  ./create-new-feature.ps1 'Implement OAuth2 integration for API'"
    exit 0
}

# Check if feature description provided
if (-not $FeatureDescription -or $FeatureDescription.Count -eq 0) {
    Write-Error "Usage: ./create-new-feature.ps1 [-Json] [-DryRun] [-AllowExistingBranch] [-ShortName <name>] <feature description>"
    exit 1
}

$featureDesc = ($FeatureDescription -join ' ').Trim()

# Validate description is not empty after trimming (e.g., user passed only whitespace)
if ([string]::IsNullOrWhiteSpace($featureDesc)) {
    Write-Error "Error: Feature description cannot be empty or contain only whitespace"
    exit 1
}

function ConvertTo-CleanBranchName {
    param([string]$Name)

    return $Name.ToLower() -replace '[^a-z0-9]', '-' -replace '-{2,}', '-' -replace '^-', '' -replace '-$', ''
}

# Load common functions (includes Get-RepoRoot and Resolve-Template)
. "$PSScriptRoot/common.ps1"

# Use common.ps1 functions which prioritize .specify
$repoRoot = Get-RepoRoot

Set-Location $repoRoot

function Get-ModuleName {
    param([string]$Description)

    $stopWords = @('i', 'a', 'an', 'the', 'to', 'for', 'of', 'in', 'on', 'at', 'by', 'with', 'from', 'is', 'are', 'add', 'fix', 'create', 'implement', 'update')
    $words = ($Description.ToLower() -replace '[^a-z0-9\s]', ' ') -split '\s+' | Where-Object { $_ -and $_.Length -ge 3 -and $stopWords -notcontains $_ }
    if (-not $words) {
        return 'general'
    }
    return $words[0]
}

function Get-FeatureId {
    param([string]$Description)

    $id = ConvertTo-CleanBranchName -Name $Description
    if ([string]::IsNullOrWhiteSpace($id)) {
        return 'feature'
    }
    return (($id -split '-') | Where-Object { $_ } | Select-Object -First 6) -join '-'
}

$developmentDir = Join-Path $repoRoot 'development'
if (-not $DryRun) {
    New-Item -ItemType Directory -Path $developmentDir -Force | Out-Null
}

# Function to generate branch name with stop word filtering and length filtering
function Get-BranchName {
    param([string]$Description)

    # Common stop words to filter out
    $stopWords = @(
        'i', 'a', 'an', 'the', 'to', 'for', 'of', 'in', 'on', 'at', 'by', 'with', 'from',
        'is', 'are', 'was', 'were', 'be', 'been', 'being', 'have', 'has', 'had',
        'do', 'does', 'did', 'will', 'would', 'should', 'could', 'can', 'may', 'might', 'must', 'shall',
        'this', 'that', 'these', 'those', 'my', 'your', 'our', 'their',
        'want', 'need', 'add', 'get', 'set'
    )

    # Convert to lowercase and extract words (alphanumeric only)
    $cleanName = $Description.ToLower() -replace '[^a-z0-9\s]', ' '
    $words = $cleanName -split '\s+' | Where-Object { $_ }

    # Filter words: remove stop words and words shorter than 3 chars (unless they're uppercase acronyms in original)
    $meaningfulWords = @()
    foreach ($word in $words) {
        # Skip stop words
        if ($stopWords -contains $word) { continue }

        # Keep words that are length >= 3 OR appear as uppercase in original (likely acronyms)
        if ($word.Length -ge 3) {
            $meaningfulWords += $word
        } elseif ($Description -cmatch "(?<![0-9A-Za-z_])$($word.ToUpper())(?![0-9A-Za-z_])") {
            # Keep short words only if they appear as uppercase in original (likely
            # acronyms). Use -cmatch so the comparison is case-sensitive, matching the
            # bash script's case-sensitive grep; -match would be case-insensitive and
            # would keep every short word. The boundaries are spelled out as ASCII
            # because .NET's \b is Unicode-aware, so an accented letter next to the
            # acronym would suppress the match that bash's LC_ALL=C grep still makes.
            $meaningfulWords += $word
        }
    }

    # If we have meaningful words, use first 3-4 of them
    if ($meaningfulWords.Count -gt 0) {
        $maxWords = if ($meaningfulWords.Count -eq 4) { 4 } else { 3 }
        $result = ($meaningfulWords | Select-Object -First $maxWords) -join '-'
        return $result
    } else {
        # Fallback to original logic if no meaningful words found
        $result = ConvertTo-CleanBranchName -Name $Description
        # @() keeps this an array. ConvertTo-CleanBranchName blanks every
        # non-[a-z0-9] character, so a description written in a non-Latin script
        # (or made only of punctuation) leaves nothing for the pipeline to
        # emit -- it yields $null, and [string]::Join on $null throws
        # ArgumentNullException. With $ErrorActionPreference = 'Stop' that is
        # terminating, so the script died with a .NET stack trace and exit 1
        # where the bash and Python twins both return an empty suffix.
        $fallbackWords = @(($result -split '-') | Where-Object { $_ } | Select-Object -First 3)
        return [string]::Join('-', $fallbackWords)
    }
}

# Generate branch name
if ($ShortName) {
    # Use provided short name, just clean it up
    $branchSuffix = ConvertTo-CleanBranchName -Name $ShortName
} else {
    # Generate from description with smart filtering
    $branchSuffix = Get-BranchName -Description $featureDesc
}

$moduleName = Get-ModuleName -Description $featureDesc
$featureId = Get-FeatureId -Description $featureDesc
$featureNum = $featureId
$branchName = "$moduleName-$featureId"

# GitHub enforces a 244-byte limit on branch names
# Validate and truncate if necessary
$originalBranchName = $branchName
if ($branchName.Length -gt $maxBranchLength) {
    $branchName = $branchName.Substring(0, $maxBranchLength).TrimEnd('-')
}
if ($branchName -ne $originalBranchName) {
    [Console]::Error.WriteLine("[specify] Warning: Branch name exceeded GitHub's 244-byte limit")
    [Console]::Error.WriteLine("[specify] Original: $originalBranchName ($($originalBranchName.Length) bytes)")
    [Console]::Error.WriteLine("[specify] Truncated to: $branchName ($($branchName.Length) bytes)")
}

$featureDir = Join-Path (Join-Path $developmentDir $moduleName) $featureId
$specFile = Join-Path $featureDir 'spec.md'
$requirementFile = Join-Path $featureDir 'requirement.md'
$serviceDir = Join-Path $featureDir 'service'

if (-not $DryRun) {
    if ((Test-Path -LiteralPath $featureDir -PathType Container) -and -not $AllowExistingBranch) {
        Write-Error "Error: Feature directory '$featureDir' already exists. Please use a different feature description or use -AllowExistingBranch."
        exit 1
    }

    $needsSpec = -not (Test-Path -PathType Leaf $specFile)
    $needsRequirement = -not (Test-Path -PathType Leaf $requirementFile)
    $specContent = if ($needsSpec) { Resolve-TemplateContent -TemplateName 'spec-template' -RepoRoot $repoRoot }
    $requirementContent = if ($needsRequirement) { Resolve-TemplateContent -TemplateName 'requirement-template' -RepoRoot $repoRoot }

    New-Item -ItemType Directory -Path $featureDir -Force | Out-Null
    New-Item -ItemType Directory -Path $serviceDir -Force | Out-Null

    if ($needsSpec) {
        if ($null -ne $specContent) {
            $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
            [System.IO.File]::WriteAllText($specFile, $specContent, $utf8NoBom)
        } else {
            # Match the bash twin (create-new-feature.sh): warn on stderr that no
            # spec template was found before creating an empty spec file, so the
            # missing-template signal is not silently swallowed on Windows.
            [Console]::Error.WriteLine("Warning: Spec template not found; created empty spec file")
            New-Item -ItemType File -Path $specFile -Force | Out-Null
        }
    }
    if ($needsRequirement -and $null -ne $requirementContent) {
        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllText($requirementFile, $requirementContent, $utf8NoBom)
    }

    # Persist to .specify/feature.json so downstream commands can find the feature
    Save-FeatureJson -RepoRoot $repoRoot -FeatureDirectory $featureDir

    # Set environment variables for the current session
    $env:SPECIFY_FEATURE = $branchName
    $env:SPECIFY_FEATURE_DIRECTORY = $featureDir

    $quotedBranchName = "'" + $branchName.Replace("'", "''") + "'"
    $quotedFeatureDir = "'" + $featureDir.Replace("'", "''") + "'"
    $featureAssignment = '$env:SPECIFY_FEATURE = ' + $quotedBranchName
    $directoryAssignment = '$env:SPECIFY_FEATURE_DIRECTORY = ' + $quotedFeatureDir
    [Console]::Error.WriteLine("# To persist: $featureAssignment")
    [Console]::Error.WriteLine("#              $directoryAssignment")
}

if ($Json) {
    $obj = [PSCustomObject]@{
        BRANCH_NAME = $branchName
        SPEC_FILE = $specFile
        FEATURE_NUM = $featureNum
        MODULE_NAME = $moduleName
        FEATURE_ID = $featureId
    }
    if ($DryRun) {
        $obj | Add-Member -NotePropertyName 'DRY_RUN' -NotePropertyValue $true
    }
    $obj | ConvertTo-Json -Compress
} else {
    Write-Output "BRANCH_NAME: $branchName"
    Write-Output "SPEC_FILE: $specFile"
    Write-Output "FEATURE_NUM: $featureNum"
    if (-not $DryRun) {
        Write-Output "# To persist in your shell: $featureAssignment"
        Write-Output "#                           $directoryAssignment"
    }
}
