import React from 'react'
import NewsList from './news_list'
import { HashRouter, Route } from 'react-router-dom'
import NewsRouteList from './news_route_list'

export default class NewsPage extends React.Component {

    constructor() {
        super()
        this.newsListRef = React.createRef()
    }

    render() {
        return (
            <HashRouter>
            <div class="mdl-grid" id='tag-page'>
                <div class="mdl-cell mdl-cell--4-col"/>
                <div class="mdl-cell mdl-cell--4-col">
                    <Route exact path="/news">
                        <NewsList ref={this.newsListRef}></NewsList>
                    </Route>
                    <NewsRouteList></NewsRouteList>
                </div>
                
                <div class="mdl-cell mdl-cell--2-col"/>
                <div class="mdl-cell mdl-cell--2-col"/>
                
            </div>
            </HashRouter>
        )
    }

    updateTagList() {
        this.newsListRef.current.setUpdateNeeded()
    }
}