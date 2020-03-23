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
                
            <Route exact path="/news">
                <NewsList ref={this.newsListRef}></NewsList>
            </Route>
            
            <NewsRouteList></NewsRouteList>
            </HashRouter>
        )
    }

    updateTagList() {
        this.newsListRef.current.setUpdateNeeded()
    }
}