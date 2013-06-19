package com.baidu.bae.api.baelog;


import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;


public class BaeAppender extends WriterAppender {
    BaeLogger logger = BaeLogger.getLogger();

    public BaeAppender() {
        super.setWriter(super.createWriter(System.out));
    }

    public BaeAppender(Layout layout) {
        setLayout(layout);
    }

    public void subAppend(LoggingEvent event) {
        StringBuffer sb = new StringBuffer();

        sb.append(this.layout.format(event));
        if (this.layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();

            if (s != null) {
                int len = s.length;

                for (int i = 0; i < len; i++) {
                    sb.append(s[i]);
                    sb.append(Layout.LINE_SEP);
                }
            }
        }
        int level = getLevel(event.getLevel());

        this.logger.log(level, sb.toString());
    }

    private static int getLevel(Level l) {
        if (l.isGreaterOrEqual(Level.ERROR)) {
            return 1;
        }

        if (l.isGreaterOrEqual(Level.WARN)) {
            return 2;
        }

        if (l.isGreaterOrEqual(Level.INFO)) {
            return 4;
        }

        if (l.isGreaterOrEqual(Level.DEBUG)) {
            return 16;
        }

        return 8;
    }
}
