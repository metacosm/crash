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

package org.crsh.term;

/**
 * An event emitted by a term.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TermEvent {

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[]";
  }

  /**
   * Signals a control-break.
   */
  public static class Close extends TermEvent {
  }

  /**
   * Signals a control-break.
   */
  public static class Break extends TermEvent {
  }

  /**
   * Signals the completion of a text line.
   */
  public static class Complete extends TermEvent {

    /** The line to be completed. */
    private CharSequence line;

    public Complete(CharSequence line) {
      this.line = line;
    }

    public CharSequence getLine() {
      return line;
    }

    @Override
    public String toString() {
      return "Complete[line=" + line + "]";
    }
  }

  /**
   * Signals a line was submitted for processing.
   */
  public static class ReadLine extends TermEvent {

    /** . */
    private final CharSequence line;

    public ReadLine(CharSequence line) {
      this.line = line;
    }

    public CharSequence getLine() {
      return line;
    }

    @Override
    public String toString() {
      return "ReadLine[line=" + line + "]";
    }
  }
}
