package data.binding.custom

import grails.testing.gorm.DataTest
import spock.lang.Specification

class BlogPostSpec extends Specification implements DataTest {

    @Override
    Class[] getDomainClassesToMock() {
        return [BlogPost, Person]
    }

    void 'can set last viewed blog post on person when the titles match'() {
        given: 'a person'
        def person = new Person(name: 'John Doe')
        assert person.save()

        and: 'a blog post'
        def blogPost = new BlogPost(title: 'Hello, World', content: 'This is a simple blog post')
        assert blogPost.save()

        when: 'we set the blog post'
        person.lastViewedPost = blogPost
        assert person.save(flush: true)
        person.refresh()

        then: 'the person has the correct blog post set'
        person.lastViewedPost == blogPost

        when: 'a new blog post with the same title is made'
        def anotherBlogPost = new BlogPost(title: blogPost.title,
                content: 'This is another blog post with the same title')
        assert anotherBlogPost.save(flush: true)

        and: 'the person sets the lastViewedPost to that new value'
        person.lastViewedPost = anotherBlogPost
        person.save(flush: true)
        person.refresh()

        then: 'the person has the correct blog post set'
        person.lastViewedPost == anotherBlogPost


    }
}
