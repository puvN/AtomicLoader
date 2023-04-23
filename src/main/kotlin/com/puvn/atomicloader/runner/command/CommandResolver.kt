package com.puvn.atomicloader.runner.command

import org.springframework.stereotype.Component

@Component
class CommandResolver {

    fun getSystemCommand(): String {
        val osName = System.getProperty(osNameProperty)
        return if (osName.startsWith(windows)) {
            "$windowsCmdCommand .\\docker\\start.bat"
        } else if (osName.startsWith(linux) || osName.startsWith(mac)) {
            "$linuxShCommand ./docker/start.sh"
        } else {
            throw UnsupportedOperationException("unsupported operating system: $osName")
        }
    }

    companion object {
        const val osNameProperty = "os.name"
        const val windows = "Windows"
        const val windowsCmdCommand = "cmd /c start"
        const val linux = "Linux"
        const val linuxShCommand = "/bin/bash "
        const val mac = "Mac"
    }

}