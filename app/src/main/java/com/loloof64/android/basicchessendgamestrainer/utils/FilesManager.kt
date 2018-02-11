/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.loloof64.android.basicchessendgamestrainer.utils

import com.loloof64.android.basicchessendgamestrainer.MyApplication
import java.io.*

object FilesManager {
    const val newLine = "\n"
    const val playerKingHeader = "# player king"
    const val computerKingHeader = "# computer king"
    const val mutualKingsHeader = "# kings mutual"
    const val otherPiecesCountHeader = "# other pieces count"
    const val parentFolderName = ".."
    const val DRAW_LINE_VALUE = "1"

    private val topDirectory = File(MyApplication.appContext.filesDir, "script")
    private var currentDirectory = topDirectory

    init {
        topDirectory.mkdir()
    }

    fun alreadyFileOrFolderInCurrentDirectory(name: String): Boolean = currentDirectory.listFiles().any { it.name == name }

    /*
    Save and overwrite text file.
     */
    fun saveTextFileInCurrentDirectory(name: String, content: String) {
        val contentLines = content.split("""\n+""".toRegex())

        val file = File(currentDirectory, name)
        BufferedWriter(FileWriter(file)).use { reader ->
            contentLines.forEach { line ->
                reader.append(line)
                reader.newLine()
            }
        }
    }

    fun createFolderInCurrentDirectory(name: String) {
        val newDirectory = File(currentDirectory, name)
        newDirectory.mkdir()
    }

    fun getCurrentDirectoryFiles() : Array<File> = currentDirectory.listFiles()
}