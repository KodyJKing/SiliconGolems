let line = ""
while (true) {
    try {
        let event = os.awaitEvent()
        let c = event.character
        if (event.isDown) {
            if (event.keycode == 14) {
                line = line.slice(0, line.length - 1)
            } else if (isPrintableChar(c)) {
                line = line + c
            }
            terminal.setLine(0, line)
        }
    } catch (err) {
        terminal.print(err)
    }
}

function isPrintableChar(c) {
    let code = c.charCodeAt(0)
    return code != 167 && code >= 32 && code != 127
}