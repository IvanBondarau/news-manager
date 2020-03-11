import React from 'react';
import $ from 'jquery'
import Tag from './tag.js'

const TAG_URL = 'http://localhost:8090/news-manager/tag'

export default class TagList extends React.Component {

    constructor() {
        super()
        this.state = {
            isLoaded: false,
            isUpdateNeeded: true,
            error: null,
            tags: []
        }
    }

    updateData() {
        $.ajax(
            {
                method: 'GET',
                url: TAG_URL
            }
        ).fail(
            (responce) => {
                this.setState(
                    {
                        isLoaded: false,
                        tags: [],
                        isUpdateNeeded: false,
                        error: {
                            code: responce.statusCode,
                            text: responce.statusText,
                            comment: responce.responseJSON.error
                        }
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        tags: responce,
                        isUpdateNeeded: false,
                        isLoaded: true,
                        error: null
                    }
                )
            }
        )
    }

    setUpdateNeeded() {
        this.setState(
            {
                tags: this.state.tags,
                isUpdateNeeded: true,
                isLoaded: this.state.isUpdateNeeded,
                error: this.state.error
            }
        )
    }

    render() {

        
        if (this.state.isUpdateNeeded) {
            this.updateData()
        }

        if (this.state.isLoaded) {
            return (
                <ul class='mdl-list' id="tags-table">
                    
                    {
                        this.state.tags.map((tag, i) => {
                            return (
                                    <li class='mdl-list__item'><Tag id={tag.id} name = {tag.name} notifyAboutChanges={this.setUpdateNeeded.bind(this)} /></li>
                                
                            )
                        })
                    }
                    
                </ul>
            )
        } else {
            if (this.state.error !== null) {
                return (
                    <div>
                        <span>Error while loading tags</span>
                        <span>Status code: {this.state.error.code}</span> 
                        <span>Text: {this.state.error.text}</span> 
                        <span>Comment: {this.state.error.comment}</span> 
                    </div>
                )
            } else {
                return (
                    <span>
                        Loading tags...
                    </span>
                )
            }
        }

    }


} 