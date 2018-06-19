import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadQueen {

class Node {
    int vertical, leftFail, rightFail;

    Node(int vertical, int leftFail, int rightFail) {
        this.vertical = vertical;
        this.leftFail = leftFail;
        this.rightFail = rightFail;
    }
}

int N = 17;
//求每个结点的竖直/撇/捺对应的值
Node[][] a;

AtomicInteger cnt = new AtomicInteger(0);

void run(int pos) {
    //描述竖直/撇/捺三种情况的占用情况
    long vertical = 0, leftFail = 0, rightFail = 0;
    int q[] = new int[N + 1];//q[i]表示第i行放在哪里
    int qi = 0;
    q[qi++] = pos;
    int delta = 2;
    if ((N & 1) == 1 && pos == N / 2) {
        delta = 1;
    }
    while (qi > 0) {
        int col = q[--qi];
        int row = qi;
        if (col == N || qi == N) {
            if (qi == N) cnt.addAndGet(delta);
            if (qi == 1) break;
            //undo operation
            col = q[--qi];
            row = qi;
            if (row >= 0) {
                vertical &= ~(1 << a[row][col].vertical);
                leftFail &= ~(1 << a[row][col].leftFail);
                rightFail &= ~(1 << a[row][col].rightFail);
            }
            q[qi++] = col + 1;
            continue;
        }
        if ((vertical & (1 << a[row][col].vertical)
                | leftFail & (1 << a[row][col].leftFail)
                | rightFail & (1 << a[row][col].rightFail)) == 0) {
            //if can put , then put it
            vertical |= 1 << a[row][col].vertical;
            leftFail |= 1 << a[row][col].leftFail;
            rightFail |= 1 << a[row][col].rightFail;
            q[qi++] = col;//reput current queen
            q[qi++] = 0;//put next line
        } else {
            q[qi++] = col + 1;
        }
    }
}

int go(int N) throws InterruptedException {
    this.N = N;
    a = new Node[N][N];
    cnt.set(0);
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            a[i][j] = new Node(j, i + j, i + N - j);
        }
    }
    Thread[] th = new Thread[(int) Math.ceil(N / 2.0)];
    for (int i = 0; i < th.length; i++) {
        final int pos = i;
        Thread thread = new Thread(() -> MultiThreadQueen.this.run(pos));
        th[i] = thread;
        thread.start();
    }
    for (Thread aTh : th) {
        aTh.join();
    }
    return cnt.get();
}

MultiThreadQueen() throws InterruptedException {
    for (int i = 1; i <= 17; i++) {
        long beg = System.currentTimeMillis();
        System.out.println(i + "=" + go(i) + ",time=" + (System.currentTimeMillis() - beg) + "ms");
    }
}

public static void main(String[] args) throws InterruptedException {
    new MultiThreadQueen();
}
}
/**
 * 1=1,time=104ms
 * 2=0,time=1ms
 * 3=0,time=0ms
 * 4=2,time=1ms
 * 5=10,time=1ms
 * 6=4,time=0ms
 * 7=40,time=2ms
 * 8=92,time=1ms
 * 9=352,time=4ms
 * 10=724,time=9ms
 * 11=2680,time=27ms
 * 12=14200,time=29ms
 * 13=73712,time=187ms
 * 14=365596,time=1040ms
 * 15=2279184,time=6264ms
 * 16=14772512,time=41212ms
 * 17=95815104,time=282789ms
 **/