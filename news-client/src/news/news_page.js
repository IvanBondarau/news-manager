import React from 'react'
import NewsList from './news_list'
import { HashRouter, NavLink, Route } from 'react-router-dom'
import NewsRouteList from './news_route_list'
import NewsCreatePage from './news-create-page'
import i18n from '../i18n'
import NewsSearchBar from './news_search_bar'
import TagCheckList from './tag_check_list'

export default class NewsPage extends React.Component {

    constructor() {
        super()
        this.newsListRef = React.createRef()
        this.authorSearch = React.createRef()
        this.tagSearchBar = React.createRef()
    }

    render() {
        return (
            <HashRouter>
                
                <Route exact path="/news">
                    <div class='mdl-grid mdl-grid--no-spacing'>
                        <div class="mdl-cell mdl-cell--4-col">
                            <NavLink className="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button" 
                                    to='/news/create'>
                                {i18n.Create}
                            </NavLink>
                            </div>
                        <div class="mdl-cell mdl-cell--4-col">
                            <h4>{i18n.Search}</h4>
                            <NewsSearchBar ref={this.authorSearch}></NewsSearchBar>
                        </div>
                        <div class="mdl-cell mdl-cell--4-col">
                            <button onClick={this.handleSearch.bind(this)} class="mdl-button mdl-js-button mdl-button--primary">
                                {i18n.Search}
                            </button>
                        </div>
                        <div class="mdl-cell mdl-cell--4-col"></div>
                        <div class="mdl-cell mdl-cell--4-col">
                            <h4>{i18n.Tags}</h4>
                            <TagCheckList ref={this.tagSearchBar} selectedTags={[]}></TagCheckList>
                        </div>
                        <div class="mdl-cell mdl-cell--4-col"></div>
                        <div class="mdl-cell mdl-cell--3-col"></div>
                        <div class="mdl-cell mdl-cell--3-col">
                            <NewsList ref={this.newsListRef}></NewsList>
                        </div>
                        
                        <div class="mdl-cell mdl-cell--6-col"></div>
                        
                    </div>
                </Route>
                
                <NewsRouteList></NewsRouteList>
                <Route exact path="/news/create">
                    <NewsCreatePage></NewsCreatePage>
                </Route>
                
            </HashRouter>
        )
    }

    handleSearch() {
        let author = this.authorSearch.current.getSelected()
        var authorString
        if (author !== '') {
            alert(author)
        } else {
            alert('Empty')
        }
    }

    updateTagList() {
        this.newsListRef.current.setUpdateNeeded()
    }
}