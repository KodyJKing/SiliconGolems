package silicongolems.gui.texteditor;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.Stack;

/**
 * The dual stack list is a list which supports fast sequential insertion and deletion in an active region.
 * Because of this, it is well suited for text editing.
 *
 * The list is represented with two stacks. The front stack holds every element before the active region,
 * the back stack represents every element after the active region. The top each stack faces the active region.
 *
 * When used in a text editor, the front stack represents every line/char before the cursor
 * and the back stack represents every line/char after the cursor. To move the cursor right,
 * an element is popped off the back stack and pushed to the front stack.
 */
public class DualStackList<T>  implements Iterable<T>{
    Stack<T> front, back;

    public DualStackList() {
        front = new Stack<T>();
        back = new Stack<T>();
    }

    public void clear() {
        front.clear();
        back.clear();
    }

    public void add(int index, T value) {
        seek(index);
        front.push(value);
    }

    public void add(T value) {
        add(size(), value);
    }

    public T get(int index) {
        if (index < front.size())
            return front.get(index);
        else
            return back.get(back.size() + front.size() - 1 - index);
    }

    public T remove(int index) {
        seek(index);
        return back.pop();
    }

    public void seek(int index) {
        if (!isValidIndex(index))
            throw new IndexOutOfBoundsException();
        while (front.size() != index) {
            int dir = (int) Math.signum(index - front.size());
            if (dir < 0)
                seekLeft();
            else
                seekRight();
        }
    }

    public void seekLeft() {
        back.push(front.pop());
    }

    public void seekRight() {
        front.push(back.pop());
    }

    public int size() {
        return front.size() + back.size();
    }

    public boolean isValidIndex(int i) {
        return i >= 0 && i <= size();
    }

    @Override
    public Iterator<T> iterator() {
        return new DualStackListIterator(this);
    }

    class DualStackListIterator implements Iterator<T>{
        DualStackList<T> list;
        int index;

        public DualStackListIterator(DualStackList<T> buf) {
            this.list = buf;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            return list.get(index++);
        }

        @Override
        public void remove() {
            throw new NotImplementedException();
        }
    }
}
