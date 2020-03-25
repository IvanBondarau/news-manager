import React from 'react'
import TagPage from './tag/tagpage.js'
import AuthorPage from './author/author_page.js'
import NewsPage from './news/news_page.js'

import i18n from './i18n'

import {
    Route,
    NavLink,
    HashRouter
  } from "react-router-dom";

class App extends React.Component {

    render() {
        return (
            <HashRouter>
            <div class="demo-layout-waterfall mdl-layout mdl-js-layout">
                <header class="mdl-layout__header mdl-layout__header--scroll mdl-color--primary">
                    <div class="mdl-layout--large-screen-only mdl-layout__header-row"/>
                    <div class="mdl-layout__tab-bar mdl-js-ripple-effect mdl-color--primary-dark">
                        <NavLink to="/" className="mdl-layout__tab">{i18n.Home}</NavLink>
                        <NavLink to="/tags" className="mdl-layout__tab">{i18n.Tags}</NavLink>
                        <NavLink to="/authors" className="mdl-layout__tab">{i18n.Authors}</NavLink>
                        <NavLink to="/news" className="mdl-layout__tab">{i18n.News}</NavLink>
                        <div class = "mdl-layout-spacer"></div>
                        <NavLink to="/" class="mdl-layout__tab" onClick={this.setEnglishLanguage.bind(this)}>EN</NavLink>
                        <NavLink to="/" class="mdl-layout__tab" onClick={this.setFrenchLanguage.bind(this)}>FR</NavLink>
                        <NavLink to="/" class="mdl-layout__tab" onClick={this.setRussianLanguage.bind(this)}>RU</NavLink>
                    </div>
                    
                </header>
                <Route path="/tags" component={TagPage}/>
                <Route path="/authors" component={AuthorPage}/>
                <Route path="/news" component={NewsPage}/>
            </div>
            </HashRouter>
                
        )
    }

    setRussianLanguage() {
        alert('Russian')
        i18n.setLanguage('ru')
        this.setState({
            language: 'ru'
        })
    }

    setEnglishLanguage() {
        alert('English')
        i18n.setLanguage('en')
        this.setState({
            language: 'en'
        })
    }

    setFrenchLanguage() {
        alert('French')
        i18n.setLanguage('fr')
        this.setState({
            language: 'fr'
        })
    }

}

export default App