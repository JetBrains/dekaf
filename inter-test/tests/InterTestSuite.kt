package org.jetbrains.dekaf.inter.test

import org.jetbrains.dekaf.inter.test.utils.ArrayHacksTest
import org.jetbrains.dekaf.inter.test.utils.SimpleStringConvertTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
        ArrayHacksTest::class,
        SimpleStringConvertTest::class,
)
class InterTestSuite
