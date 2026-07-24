param(
    [string]$Path = "."
)

$extensions = @(
    ".cs",
    ".java",
    ".kt",
    ".js",
    ".jsx",
    ".ts",
    ".tsx",
    ".py",
    ".sql",
    ".html",
    ".css",
    ".scss",
    ".xml",
    ".json",
    ".yml",
    ".yaml",
    ".ps1"
)

$excludedDirectories = @(
    ".git",
    ".idea",
    ".vs",
    "bin",
    "obj",
    "node_modules",
    "dist",
    "build",
    "target",
    "packages",
    "coverage"
)

$results = @(
    Get-ChildItem -Path $Path -File -Recurse -ErrorAction SilentlyContinue |
        Where-Object {
            $file = $_
            $pathParts = $file.FullName -split "[\\/]"
            $hasAllowedExtension = $extensions -contains $file.Extension.ToLower()
            $isExcluded = $false

            foreach ($directory in $excludedDirectories) {
                if ($pathParts -contains $directory) {
                    $isExcluded = $true
                    break
                }
            }

            $hasAllowedExtension -and -not $isExcluded
        } |
        ForEach-Object {
            $file = $_

            try {
                $lines = @([System.IO.File]::ReadAllLines($file.FullName))
                $nonEmptyLines = @(
                    $lines | Where-Object {
                        -not [string]::IsNullOrWhiteSpace($_)
                    }
                )

                [PSCustomObject]@{
                    Extension     = $file.Extension.ToLower()
                    File          = $file.FullName
                    TotalLines    = $lines.Count
                    NonEmptyLines = $nonEmptyLines.Count
                }
            }
            catch {
                Write-Warning "Nie udalo sie odczytac pliku: $($file.FullName)"
            }
        }
)

$totalLines = ($results | Measure-Object -Property TotalLines -Sum).Sum
$totalNonEmptyLines = ($results | Measure-Object -Property NonEmptyLines -Sum).Sum

if ($null -eq $totalLines) {
    $totalLines = 0
}

if ($null -eq $totalNonEmptyLines) {
    $totalNonEmptyLines = 0
}

Write-Host ""
Write-Host "WYNIK REPOZYTORIUM"
Write-Host "=================="
Write-Host "Liczba plikow:   $($results.Count)"
Write-Host "Wszystkie linie: $totalLines"
Write-Host "Niepuste linie:  $totalNonEmptyLines"
Write-Host ""

Write-Host "PODZIAL NA ROZSZERZENIA"
Write-Host "======================="

$results |
    Group-Object -Property Extension |
    ForEach-Object {
        [PSCustomObject]@{
            Extension = $_.Name
            Files = $_.Count
            TotalLines = ($_.Group | Measure-Object -Property TotalLines -Sum).Sum
            NonEmptyLines = ($_.Group | Measure-Object -Property NonEmptyLines -Sum).Sum
        }
    } |
    Sort-Object -Property TotalLines -Descending |
    Format-Table -AutoSize