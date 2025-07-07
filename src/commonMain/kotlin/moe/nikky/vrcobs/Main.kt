package moe.nikky.vrcobs

import com.github.ajalt.clikt.command.main
import kotlinx.coroutines.runBlocking
import moe.nikky.vrcobs.cli.RootCommand

fun main(args: Array<String>) = runBlocking {
    RootCommand()
        .main(args)
}