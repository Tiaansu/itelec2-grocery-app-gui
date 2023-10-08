const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

fs.readdir(__dirname, (err, files) => {
    if (err) {
        console.err(`Error reading source folder: ${err}`);
        return;
    }

    files.forEach((file) => {
        if (fs.statSync(path.join(__dirname, file)).isFile()) {
            if (file.endsWith('.js')) {
                const readStream = fs.createReadStream(path.join(__dirname, file));
                const writeStream = fs.createWriteStream(path.join(__dirname, '../final', file));
            
                readStream.pipe(writeStream);
                
                readStream.on('error', (err) => {});
                writeStream.on('error', (err) => {});

                writeStream.on('finish', () => {
                    console.log(`Copied ${path.join(__dirname, file)} to ${path.join(__dirname, '../final', file)}`);
                })
            }
        }
    });
});