package data.binding.custom

import grails.rest.Resource

@Resource(uri = "/api/people")
class Person {

    String name
    BlogPost lastViewedPost

    static constraints = {
        lastViewedPost nullable: true
    }
}
