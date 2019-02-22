package data.binding.custom

import grails.rest.Resource
import groovy.transform.Sortable

@Resource(uri = '/api/blog-posts')
@Sortable(includes = ['title'])
class BlogPost {

    String title
    String content

    Date dateCreated
    Date lastUpdated

    static constraints = {
    }

    static mapping = {
        content type: 'text'
    }
}
