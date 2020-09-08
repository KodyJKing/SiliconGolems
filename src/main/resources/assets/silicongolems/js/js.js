//var
//    BACKSPACE = 14,
//    ENTER_A = 28,
//    ENTER_B = 156,
//    TAB = 15
//    ;
//
//function flushEvents() {
//    while ( os.dequeueEvent() ) { }
//}
//
//function shiftTextUp() {
//    terminal.setShift(terminal.getShift() + 1)
//}
//
//function isPrintableChar( c ) {
//    let code = c.charCodeAt( 0 )
//    return code != 167 && code >= 32 && code != 127
//}
//
//function clear() {
//    for (let y = 0; y < terminal.getHeight(); y++)
//        terminal.setLine(y, "")
//}
//
//function getInput() {
//    let input = ""
//    flushEvents()
//    shiftTextUp()
//    displayInput()
//    while ( true ) {
//        try {
//            let event = os.awaitEvent()
//            let k = event.keycode
//            let c = event.character
//            if ( event.isDown ) {
//                if ( k == BACKSPACE )
//                    input = input.slice( 0, input.length - 1 )
//                else if ( k == ENTER_A || k == ENTER_B )
//                    return input
//                else if ( k == TAB )
//                    type( "    " )
//                else if ( isPrintableChar( c ) )
//                    type( c )
//                displayInput()
//            }
//        } catch ( e ) {
//            os.log(e)
//        }
//    }
//
//    function type( text ) {
//        input = input + text
//    }
//
//    function displayInput() {
//        let displayLine = ">" + input.slice( 0, terminal.getWidth() - 1 )
//        terminal.setLine( terminal.getHeight() - 1, displayLine )
//    }
//}
//
//while ( true ) {
//    let input = getInput()
//    try {
//        let result = eval(input)
//        if (result !== undefined)
//            terminal.print( result )
//    } catch (e) {
//        terminal.print(e)
////        terminal.print("could not eval input")
//    }
//}
//
//terminal.print("done")

var f = os.fs.open("/assets/silicongolems/js/js.js", true, false, false)
terminal.print(f.readAll())