import React from 'react';
import $ from 'jquery'
import NewsCard from './news_card.js'

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsList extends React.Component {

    constructor() {
        super()
        this.state = {
            isLoaded: false,
            isUpdateNeeded: true,
            error: null,
            news: [],
            page: 1,
            pageSize: 5
        }
    }

    updateData() {
        var firstParam = true;
        var searchUrl = NEWS_URL
        if (this.props.author !== null) {

            firstParam = false
            searchUrl += "?authorName=" + this.props.author.name + "&authorSurname=" + this.props.author.surname
        }
        if (this.props.tags !== null && this.props.tags.length>0) {
            var tagNames = this.props.tags.map(tag => tag.name).join(',')

            if (firstParam) {
                searchUrl += "?tagNames="+tagNames
            } else {
                searchUrl += "&tagNames="+tagNames
            }
            firstParam = false;
        } 

        if (firstParam) {
            searchUrl += "?page=" + this.props.page + "&limit=" + this.props.limit;
        } else {
            searchUrl += "&page=" + this.props.page + "&limit=" + this.props.limit;
        }

        $.ajax(
            {
                method: 'GET',
                url: searchUrl
            }
        ).fail(
            (responce) => {
                this.setState(
                    {
                        isLoaded: false,
                        news: [],
                        isUpdateNeeded: false,
                        error: {
                            code: responce.statusCode,
                            text: responce.statusText,
                            comment: null
                        },
                        page: this.state.page,
                        pageSize: this.state.pageSize
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        news: responce,
                        isUpdateNeeded: false,
                        isLoaded: true,
                        error: null,
                        page: this.state.page,
                        pageSize: this.state.pageSize
                    }
                )
            }
        )
    }

    setUpdateNeeded() {
        this.setState(
            {
                news: this.state.news,
                isUpdateNeeded: true,
                isLoaded: false,
                error: this.state.error,
                page: this.state.page,
                pageSize: this.state.pageSize
            }
        )
    }

    render() {

        
        if (this.state.isUpdateNeeded) {
            this.updateData()
        }

        if (this.state.isLoaded) {
            return (
                <div class="mdl-grid mdl-grid--no-spacing" id="news-table">
                    
                    {
                        this.state.news.map((newsItem, i) => {
                            return (
                                <div class='mdl-cell--12-col'>
                                    <NewsCard id={newsItem.id} 
                                              item={newsItem} 
                                              notifyAboutChanges={this.setUpdateNeeded.bind(this)} />
                                </div>
                            )
                        })
                    }
                    
                </div>
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
                        Loading news...
                    </span>
                )
            }
        }

    }






} 