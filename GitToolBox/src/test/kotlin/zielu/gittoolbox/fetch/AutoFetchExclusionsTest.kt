package zielu.gittoolbox.fetch

import com.intellij.mock.MockVirtualFile
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import zielu.gittoolbox.config.AutoFetchExclusionConfig
import zielu.gittoolbox.config.RemoteConfig
import zielu.intellij.test.createRemote
import java.util.function.Supplier

@ExtendWith(MockKExtension::class)
internal class AutoFetchExclusionsTest {
  private val repoRoot: VirtualFile = MockVirtualFile("repo-root")
  @MockK
  private lateinit var repository: GitRepository

  @BeforeEach
  fun beforeEach() {
    every { repository.root } returns repoRoot
  }

  @Test
  fun repoWithoutSpecifiedRemotesIsExcluded() {
    // given
    val exclusionsMap: Map<String, AutoFetchExclusionConfig> = mapOf(
      repoRoot.url to AutoFetchExclusionConfig(repoRoot.url)
    )
    val exclusions = AutoFetchExclusions(Supplier { exclusionsMap })

    // when
    val result = exclusions.apply(listOf(repository))

    // then
    assertThat(result).isEmpty()
  }

  @Test
  fun notConfiguredRepoIsNotExcluded() {
    // given
    val exclusions = AutoFetchExclusions(Supplier { mapOf<String, AutoFetchExclusionConfig>() })

    // when
    val result = exclusions.apply(listOf(repository))

    // then
    assertThat(result).containsOnly(repository)
  }

  @Test
  fun repoWithSpecifiedRemoteExcludesItFromRemotes() {
    // given
    val upstream = createRemote("upstream")
    val origin = createRemote("origin")
    every { repository.remotes } returns listOf(upstream, origin)
    val exclusionsMap: Map<String, AutoFetchExclusionConfig> = mapOf(
      repoRoot.url to AutoFetchExclusionConfig(repoRoot.url, mutableListOf(RemoteConfig(upstream.name)))
    )
    val exclusions = AutoFetchExclusions(Supplier { exclusionsMap })

    // when
    val result = exclusions.apply(listOf(repository))
    assertSoftly { softly ->
      softly.assertThat(result).hasSize(1)
      softly.assertThat(result[0].remotes).containsOnly(origin)
    }
  }
}
