package com.github.cdcalc

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BranchParserTest {
    lateinit var parser: BranchParser

    @Before
    fun before() {
        parser = BranchParser()
    }

    @Test
    fun shouldBeAbleToParseRelease1_2_3() {
        assertEquals(Tag(1,2,3), parser.findTagInBranchName("release/1.2.3"))
    }

    @Test
    fun shouldBeAbleToParseRelease5_7_1() {
        assertEquals(Tag(5,7,1), parser.findTagInBranchName("release/5.7.1"))
    }

    @Test fun shouldReturnEmptyTagIfNotAReleaseBranch() {
        assertEquals(Tag.Empty, parser.findTagInBranchName("master"))
    }

    @Test fun shouldFail() {
        assertEquals("foo", "foo")
    }

    /***
     * NOTE: we're only aiming for the super duper happy path at the moment
     * Partial branch names are not supported at the moment
     */
    @Test fun shouldReturnEmptyIfPartialNamesAreUsed() {
        assertEquals(Tag.Empty, parser.findTagInBranchName("release/5.7"))
    }
}
