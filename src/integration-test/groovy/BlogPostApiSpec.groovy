import grails.testing.mixin.integration.Integration
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.DefaultHttpClient
import spock.lang.Specification

import static io.micronaut.http.HttpRequest.POST
import static io.micronaut.http.HttpRequest.PUT

@Integration
class BlogPostApiSpec extends Specification {

    BlockingHttpClient client

    def setup() {
        client = new DefaultHttpClient("http://localhost:${serverPort}".toURL()).toBlocking()
    }

    def 'person can set latest post to one of the same title'() {
        given: 'a person is created'
        def personResponse = client.exchange(POST('/api/people', [name: 'John Doe']), Map)
        assert personResponse.status() == HttpStatus.CREATED

        def personId = personResponse.body().id
        assert personId

        and: 'a blog post is created'
        def blogPostResponse = client.exchange(POST('/api/blog-posts', [
                title: 'Hello, World!',
                content: 'This is a sample blog post'
        ]), Map)
        assert blogPostResponse.status() == HttpStatus.CREATED

        def blogPostId = blogPostResponse.body().id
        assert blogPostId

        when: 'the lastViewedPost is set on the person'
        def setLastViewedResponse = client.exchange(PUT("/api/people/${personId}", [lastViewedPost: blogPostId]), Map)
        assert setLastViewedResponse.status() == HttpStatus.OK

        and: 'the person is retrieved again'
        def personJson = client.retrieve("/api/people/${personId}", Map)

        then:
        personJson.lastViewedPost.id == blogPostId

        when: 'another blog post is created with the same title'
        def anotherBlogPostResponse = client.exchange(POST('/api/blog-posts', [
                title: 'Hello, World!',
                content: 'This is another blog post with the same title, but different content'
        ]), Map)
        assert anotherBlogPostResponse.status() == HttpStatus.CREATED

        def anotherBlogPostId = anotherBlogPostResponse.body()

        and: 'the lastViewed Post is set on the person'
        setLastViewedResponse = client.exchange(PUT("/api/people/${personId}", [lastViewedPost: anotherBlogPostId]), Map)
        assert setLastViewedResponse.status() == HttpStatus.OK

        and: 'the person is retrieved again'
        personJson = client.retrieve("/api/people/${personId}", Map)

        then:
        personJson.lastViewedPost.id == anotherBlogPostId

    }
}
