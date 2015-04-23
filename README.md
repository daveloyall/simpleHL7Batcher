# simpleHL7Batcher
This tool bundles individual HL7 messages into batches (which aren't fully specification compliant).

The number of files per batch is configurable.

This tool will ignore very recently modified files, to avoid touching files that are still being written to.

Input files are moved into an archive directory after creating the batches.  There is one subdirectory per batch file, with matching names.

## Usage
    Usage: java -jar simpleHL7Batcher.jar [options]
        --archive
           Archive directory
        --batchsize
           Number of files per batch
               Default: 200
        --cooldown
           Ignore files modified less than [cooldown] seconds ago.
           (This avoids partial files.)
               Default: 30
        --input
           Input directory
        --output
           Output directory
