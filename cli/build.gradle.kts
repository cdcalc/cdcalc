plugins {
    application
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

application {
    mainClassName = "com.github.cdcalc.cli.ApplicationKt"
}
