import React from 'react'
import AuthorList from './author_list.js'
import CreateAuthorForm from './author_form.js'

export default class TagPage extends React.Component{
    constructor() {
        super()
        this.tagListRef = React.createRef()
    }

    render() {
        return (
            <div class="mdl-grid" id='tag-page'>
                <div class="mdl-cell mdl-cell--2-col"/>
                <div class="mdl-cell mdl-cell--5-col">
                    <AuthorList ref={this.tagListRef}/>
                </div>
                
                <div class="mdl-cell mdl-cell--1-col"/>
                <div class="mdl-cell mdl-cell--2-col"> 
                    <CreateAuthorForm update={this.updateAuthorList.bind(this)}/>
                </div>
                <div class="mdl-cell mdl-cell--2-col"/>

            </div>
        )
    }

    updateAuthorList() {
        this.tagListRef.current.setUpdateNeeded()
    }
}