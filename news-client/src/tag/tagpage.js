import TagList from './taglist.js'
import React from 'react'
import CreateTagForm from './createtag.js'

export default class TagPage extends React.Component{
    constructor() {
        super()
        this.tagListRef = React.createRef()
    }

    render() {
        return (
            <div class="mdl-grid" id='tag-page'>
                <div class="mdl-cell mdl-cell--4-col"/>
                <div class="mdl-cell mdl-cell--4-col">
                    <TagList ref={this.tagListRef}/>
                </div>
                
                <div class="mdl-cell mdl-cell--2-col"/>
                <div class="mdl-cell mdl-cell--2-col">
                    <CreateTagForm update={this.updateTagList.bind(this)}/>
                </div>
            </div>
        )
    }

    updateTagList() {
        this.tagListRef.current.setUpdateNeeded()
    }
}