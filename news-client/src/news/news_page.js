import React from 'react'
import NewsList from './news_list'
import { HashRouter, NavLink, Route } from 'react-router-dom'
import NewsRouteList from './news_route_list'
import NewsCreatePage from './news-create-page'
import i18n from '../i18n'
import NewsSearchBar from './news_search_bar'
import TagCheckList from './tag_check_list'

import $ from 'jquery'

const NEWS_URL = 'http://localhost:8080/news-manager/news'

const AVAILABLE_PAGE_SIZES = [5, 10, 20, 50, 100]

export default class NewsPage extends React.Component {

    constructor() {
        super()
        this.state =
            {
                newsListRef: React.createRef(),
                authorSearch: React.createRef(),
                tagSearchBar: React.createRef(),
                tags: [],
                author: null,
                pageSize : 5,
                page: 1,
                isLoaded: false,
                count: null
            }
    }

    render() {
        var pagesCount = this.state.count/this.state.pageSize
            + (this.state.count % this.state.pageSize !== 0 ? 1 : 0)
        if (this.state.isLoaded) {
            return (
                <HashRouter>
                    
                    <Route exact path="/news">
                        <div class='mdl-grid mdl-grid--no-spacing'>
                            <div class="mdl-cell mdl-cell--2-col">
                                <NavLink className="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-create-button" 
                                        to='/news/create'>
                                    {i18n.Create}
                                </NavLink>
                            </div>
                            
                            <div class="mdl-cell mdl-cell--4-col">
                                <NewsList ref={this.state.newsListRef} 
                                    page={this.state.page} limit={this.state.pageSize} 
                                    tags={this.state.tags} author={this.state.author}></NewsList>
                            </div>
                            
                            <div class="mdl-cell mdl-cell--1-col"></div>
                            <div class="mdl-cell mdl-cell--4-col">
                                <h4>{i18n.Search}</h4>
                                <NewsSearchBar ref={this.state.authorSearch}></NewsSearchBar>
                                <h4>{i18n.Tags}</h4>
                                <TagCheckList ref={this.state.tagSearchBar} selectedTags={this.state.tags}></TagCheckList>
                                <div class="mdl-textfield mdl-js-textfield">
                                    <label class="mdl-textfield__label" htmlFor="page-select">{i18n.Page}</label>
                                    <select id="page-select" class="mdl-textfield__input"
                                        onChange={this.handlePageChange.bind(this)}>
                                        {
                                            this.range(1, pagesCount).map(
                                                item => {
                                                    if (item === this.state.page){
                                                        return <option value={item} selected>{item}</option>
                                                    }  else {
                                                        return <option value={item}>{item}</option>
                                                    }
                                                }
                                            )
                                        }
                                    </select>
                                </div>
                                <div class="mdl-textfield mdl-js-textfield">
                                    <select id="page-size-select" class="mdl-textfield__input"
                                        onChange={this.handlePageSizeChange.bind(this)}>
                                        {
                                            AVAILABLE_PAGE_SIZES.map(
                                                item => {
                                                    if (item == this.state.pageSize){
                                                        return <option value={item} selected>{item}</option>
                                                    }  else {
                                                        return <option value={item}>{item}</option>
                                                    }
                                                }
                                            )
                                        }
                                    </select>
                                    
                                    <label class="mdl-textfield__label" htmlFor="page-size-select">{i18n.PageSize}</label>
                                </div>
                                <br/>
                                <button onClick={this.handleSearch.bind(this)} class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored">
                                    {i18n.Search}
                                </button>
                            </div>
                            
                            <div class="mdl-cell mdl-cell--1-col"></div>
                            
                        </div>
                    </Route>
                    
                    <NewsRouteList></NewsRouteList>
                    <Route exact path="/news/create">
                        <NewsCreatePage></NewsCreatePage>
                    </Route>
                    
                </HashRouter>
            )
        } else {
            this.countNews()
            return <div></div>
        }
    }

    handleSearch() {

        this.setState({
            newsListRef: this.state.newsListRef,
            authorSearch: this.state.authorSearch,
            tagSearchBar: this.state.tagSearchBar,
            tags: this.state.tagSearchBar.current.getSelected(),
            author: this.state.authorSearch.current.getSelected(),
            pageSize : this.state.pageSize,
            page: 1,
            isLoaded: false,
            count: null
        })
        
    }

    updateNewsList() {
        this.state.newsListRef.current.setUpdateNeeded()
    }

    range(start, end) {
        var ans = [];
        for (let i = start; i <= end; i++) {
            ans.push(i);
        }
        return ans;
    }

    countNews() {

        $.ajax({
            method: 'GET',
            url: NEWS_URL + "/count"
        }).fail(
            (responce) => {
                this.setState(
                    {
                        newsListRef: this.state.newsListRef,
                        authorSearch: this.state.authorSearch,
                        tagSearchBar: this.state.tagSearchBar,
                        tags: this.state.tags,
                        author: this.state.author,
                        pageSize : this.state.pageSize,
                        page: this.state.page,
                        isLoaded: false,
                        count: null
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        newsListRef: this.state.newsListRef,
                        authorSearch: this.state.authorSearch,
                        tagSearchBar: this.state.tagSearchBar,
                        tags: this.state.tags,
                        author: this.state.author,
                        pageSize : this.state.pageSize,
                        page: this.state.page,
                        isLoaded: true,
                        count: responce
                    }
                )
            }
        )

        
    }

    handlePageSizeChange() {
        var newPageSize = $('#page-size-select').children("option:selected").val();
        this.setState(
            {
                newsListRef: this.state.newsListRef,
                authorSearch: this.state.authorSearch,
                tagSearchBar: this.state.tagSearchBar,
                pageSize : newPageSize,
                page: 1,
                isLoaded: false,
                count: null
            }
        )
    }

    handlePageChange() {
        var newPage = $('#page-select').children("option:selected").val();
        this.setState(
            {
                newsListRef: this.state.newsListRef,
                authorSearch: this.state.authorSearch,
                tagSearchBar: this.state.tagSearchBar,
                pageSize : this.state.pageSize,
                page: newPage,
                isLoaded: false,
                count: null
            }
        )
    }
}