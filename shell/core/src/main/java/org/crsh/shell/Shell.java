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

package org.crsh.shell;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface Shell {

  /**
   * Returns the welcome message.
   *
   * @return the welcome message
   */
  String getWelcome();

  /**
   * Returns the shell prompt.
   *
   * @return the shell prompt
   */
  String getPrompt();

  /**
   * Process a request.
   *
   * @param request the request to process
   * @param processContext the process context
   */
  void process(String request, ShellProcessContext processContext);

  /**
   * Completion.
   *
   *
   * @param prefix the prefix to complete
   * @return the sorted list of available suffixes
   */
  Map<String, String> complete(String prefix);

}
