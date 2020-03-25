import React from 'react'
import NewsList from './news_list'
import { HashRouter, NavLink, Route } from 'react-router-dom'
import NewsRouteList from './news_route_list'
import NewsCreatePage from './news-create-page'
import i18n from '../i18n'

export default class NewsPage extends React.Component {

    constructor() {
        super()
        this.newsListRef = React.createRef()
    }

    render() {
        return (
            <HashRouter>
                
                <Route exact path="/news">
                    <div class='mdl-grid mdl-grid--no-spacing'>
                        
                    <NavLink className="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button" 
                            to='/news/create'>
                        {i18n.Create}
                    </NavLink>
                        
                        <div class="mdl-cell mdl-cell--12-col">
                            <NewsList ref={this.newsListRef}></NewsList>
                        </div>

                        
                        
                    </div>
                </Route>
                
                <NewsRouteList></NewsRouteList>
                <Route exact path="/news/create">
                    <NewsCreatePage></NewsCreatePage>
                </Route>
                
            </HashRouter>
        )
    }

    updateTagList() {
        this.newsListRef.current.setUpdateNeeded()
    }
}