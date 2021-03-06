/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.ssh.term;

import org.crsh.term.CodeType;
import org.crsh.term.spi.TermIO;
import org.crsh.util.OutputCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SSHIO implements TermIO {

  /** Copied from net.wimpi.telnetd.io.TerminalIO. */
  private static final int UP = 1001;

  /** Copied from net.wimpi.telnetd.io.TerminalIO. */
  private static final int DOWN = 1002;

  /** Copied from net.wimpi.telnetd.io.TerminalIO. */
  private static final int HANDLED = 1305;

  /** . */
  private static final Logger log = LoggerFactory.getLogger(SSHIO.class);

  /** . */
  private static final String DEL_SEQ = OutputCode.DELETE_PREV_CHAR + " " + OutputCode.DELETE_PREV_CHAR;

  /** . */
  private final Reader reader;

  /** . */
  private final Writer writer;

  /** . */
  private static final int STATUS_NORMAL = 0;

  /** . */
  private static final int STATUS_READ_ESC_1 = 1;

  /** . */
  private static final int STATUS_READ_ESC_2 = 2;

  /** . */
  private int status;

  /** . */
  private final CRaSHCommand command;

  /** . */
  final AtomicBoolean closed;

  public SSHIO(CRaSHCommand command) {
    this.command = command;
    this.writer = new OutputStreamWriter(command.out);
    this.reader = new InputStreamReader(command.in);
    this.status = STATUS_NORMAL;
    this.closed = new AtomicBoolean(false);
  }

  public int read() throws IOException {
    while (true) {
      if (closed.get()) {
        return HANDLED;
      } else {
        int r = reader.read();
        switch (status) {
          case STATUS_NORMAL:
            if (r == 27) {
              status = STATUS_READ_ESC_1;
            } else {
              return r;
            }
            break;
          case STATUS_READ_ESC_1:
            if (r == 91) {
              status = STATUS_READ_ESC_2;
            } else {
              status = STATUS_NORMAL;
              log.error("Unrecognized stream data " + r + " after reading ESC code");
            }
            break;
          case STATUS_READ_ESC_2:
            status = STATUS_NORMAL;
            switch (r) {
              case 65:
                return UP;
              case 66:
                return DOWN;
              case 67:
                // Swallow RIGHT
                break;
              case 68:
                // Swallow LEFT
                break;
              default:
                log.error("Unrecognized stream data " + r + " after reading ESC+91 code");
                break;
            }
        }
      }
    }
  }

  public int getWidth() {
    return command.getContext().getWidth();
  }

  public CodeType decode(int code) {
    if (code == command.getContext().verase) {
      return CodeType.DELETE;
    } else {
      switch (code) {
        case HANDLED:
          return CodeType.CLOSE;
        case 3:
          return CodeType.BREAK;
        case 9:
          return CodeType.TAB;
        case UP:
          return CodeType.UP;
        case DOWN:
          return CodeType.DOWN;
        default:
          return CodeType.CHAR;
      }
    }
  }

  public void close() {
    if (closed.get()) {
      log.debug("Attempt to closed again");
    } else {
      log.debug("Closing SSHIO");
      command.session.close(false);
    }
  }

  public void flush() throws IOException {
    writer.flush();
  }

  public void write(String s) throws IOException {
    writer.write(s);
  }

  public void write(char c) throws IOException {
    writer.write(c);
  }

  public void writeDel() throws IOException {
    writer.write(DEL_SEQ);
  }

  public void writeCRLF() throws IOException {
    writer.write("\r\n");
  }

  public boolean moveRight(char c) throws IOException {
    return false;
  }

  public boolean moveLeft() throws IOException {
    return false;
  }
}
