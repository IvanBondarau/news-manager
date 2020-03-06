## SonarQube
http://EPBYMINW7592:9000/dashboard?id=com.epam.lab%3Anews
## Jenkins
http://EPBYMINW7592:8080/job/news-manager/  
login: developer  
password: developer_password
## Tomcat
http://EPBYMINW7592:8090/news-manager/  
## API examples
#### News
* GET /news - get all news  
* GET /news?tagNames={tag_name_1},{tag_name_2},...&orderBy=[date|author|tags] - search and sort news by sspecified params 
* GET /news/id - get news by id 
* POST /news - create news. 
Request body example:
{
&nbsp;&nbsp;&nbsp;&nbsp;"title": "news x",
&nbsp;&nbsp;&nbsp;&nbsp;"shortText": "shortText",
&nbsp;&nbsp;&nbsp;&nbsp;"fullText": "fullText",
&nbsp;&nbsp;&nbsp;&nbsp;"tags": [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "test"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
   &nbsp;&nbsp;&nbsp;&nbsp; ],
    &nbsp;&nbsp;&nbsp;&nbsp;"author": {
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name" : "name",
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"surname" : "surname"
    &nbsp;&nbsp;&nbsp;&nbsp;}
}
If news contains new tags or new author, that should be created with news, then this tag/author id should be omitted (as in example).
Otherwise, tag's/author's id should be provided.
* PUT /news/id - update news with specified id. 
* DELETE /news/id - delete news by id

### Author
* GET /author - get all authors  
* GET /author/id - get author by id 
* POST /author - create new author
* PUT /author/id - update author with specified id
* DELETE /author/id - delete author by id

### Tag
* GET /tag - get all tags  
* GET /tag/id - get tag by id 
* POST /tag - create new tag. **All tag names should be unique**
* PUT /tag/id - update tag with specified id
* DELETE /tag/id - delete tag by id