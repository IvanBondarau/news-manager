import React from 'react';
import $ from 'jquery'
import NewsFullCard from './news_full_card.js'
import { Route } from 'react-router-dom';

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsRouteList extends React.Component {

    constructor() {
        super()
        this.state = {
            isLoaded: false,
            isUpdateNeeded: true,
            error: null,
            news: []
        }
    }

    updateData() {
        $.ajax(
            {
                method: 'GET',
                url: NEWS_URL
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
                        }
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
                        error: null
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
                        this.state.news.map((newsItem, i) => {
                            return (
                                <Route exact path={'/news/' + newsItem.id}>
                                    <NewsFullCard item={newsItem}></NewsFullCard>
                                </Route>
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
                        Loading news...
                    </span>
                )
            }
        }

    }


} 