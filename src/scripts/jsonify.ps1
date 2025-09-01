$filePath = ".\questions_icebreakers_raw.txt"
$jsonArray = @()
$lines = Get-Content $filePath
$id = 0

foreach ($line in $lines) {
    $jsonObject = @{
        text = $line.Trim()
        id = $id
    }
    $id++
    $jsonArray += $jsonObject
}

$jsonOutput = $jsonArray | ConvertTo-Json -Depth 3
$jsonOutput | Set-Content ".\output.json"
$jsonOutput
