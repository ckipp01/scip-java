package tests

import java.nio.file.FileSystems
import java.nio.file.Path

import scala.meta.internal.io.FileIO
import scala.meta.io.AbsolutePath

import com.sourcegraph.lsif_java.LsifJava
import com.sourcegraph.lsif_java.buildtools.ClasspathEntry
import moped.testkit.DeleteVisitor
import moped.testkit.FileLayout
import moped.testkit.MopedSuite
import munit.TestOptions

abstract class BaseBuildToolSuite extends MopedSuite(LsifJava.app) {
  override def environmentVariables: Map[String, String] = sys.env

  // NOTE(olafur): workaround for https://github.com/scalameta/moped/issues/18
  override val temporaryDirectory: DirectoryFixture =
    new DirectoryFixture {
      private val path = BuildInfo.temporaryDirectory.toPath
      override def apply(): Path = path
      override def beforeEach(context: BeforeEach): Unit = {
        DeleteVisitor.deleteRecursively(path)
      }
    }

  private val semanticdbPattern = FileSystems
    .getDefault
    .getPathMatcher("glob:**.semanticdb")

  def checkBuild(
      options: TestOptions,
      original: String,
      expectedSemanticdbFiles: Int = 0,
      extraArguments: List[String] = Nil,
      expectedError: String = "",
      expectedPackages: String = ""
  ): Unit = {
    test(options) {
      FileLayout.fromString(original, root = workingDirectory)
      val targetroot = workingDirectory.resolve("targetroot")
      val arguments =
        List("index", "--targetroot", targetroot.toString) ++ extraArguments
      val exit = app().run(arguments)
      if (expectedError.nonEmpty) {
        assert(clue(exit) != 0, clues(app.capturedOutput))
        assertNoDiff(app.capturedOutput, expectedError)
      } else {
        assertEquals(exit, 0, clues(app.capturedOutput))
      }
      val semanticdbFiles = FileIO
        .listAllFilesRecursively(AbsolutePath(targetroot))
        .filter(p => semanticdbPattern.matches(p.toNIO))
      if (semanticdbFiles.length != expectedSemanticdbFiles) {
        fail(
          s"Expected $expectedSemanticdbFiles SemanticDB file(s) to be generated.",
          clues(semanticdbFiles)
        )
      }
      if (expectedPackages.nonEmpty) {
        val obtainedPackages = ClasspathEntry
          .fromTargetroot(targetroot)
          .map(_.toPackageHubId)
          .sorted
          .distinct
          .mkString("\n")
        assertNoDiff(obtainedPackages, expectedPackages)
      }
    }
  }

}
