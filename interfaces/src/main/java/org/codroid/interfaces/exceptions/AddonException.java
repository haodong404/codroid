/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.interfaces.exceptions;


import org.codroid.interfaces.log.Logger;

/**
 * This is a superclass that should only be inherited by the exceptions about addon.
 */
public class AddonException extends Exception {

    /**
     * Print the stack trace by addon logger.
     *
     * @param logger which logger you want to use.
     */
    public void printStackTrace(Logger logger) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.toString());
        for (var i : getStackTrace()) {
            builder.append("\n\tat ");
            builder.append(i.toString());
        }
        logger.e(builder.toString());
    }
}
