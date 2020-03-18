import React from 'react';
import $ from 'jquery'
import Author from './author.js'

const AUTHOR_URL = 'http://localhost:8080/news-manager/author'

export default class AuthorList extends React.Component {

    constructor() {
        super()
        this.state = {
            isLoaded: false,
            isUpdateNeeded: true,
            error: null,
            authors: []
        }
    }

    updateData() {
        $.ajax(
            {
                method: 'GET',
                url: AUTHOR_URL
            }
        ).fail(
            (responce) => {
                this.setState(
                    {
                        isLoaded: false,
                        authors: [],
                        isUpdateNeeded: false,
                        error: {
                            code: responce.statusCode,
                            text: responce.statusText,
                            comment: null
                        }
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        authors: responce,
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
                authors: this.state.tags,
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
                        this.state.authors.map((author, i) => {
                            return (
                                <li class='mdl-list__item'>
                                    <Author id={author.id} 
                                            name={author.name} 
                                            surname={author.surname} 
                                            notifyAboutChanges={this.setUpdateNeeded.bind(this)} />
                                </li>
                                
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
                        Loading authors...
                    </span>
                )
            }
        }

    }


} 