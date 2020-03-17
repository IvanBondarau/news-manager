import React from 'react'
import TagPage from './tag/tagpage.js'

import {
    Route,
    NavLink,
    HashRouter
  } from "react-router-dom";

export default class App extends React.Component {

    render() {
        return (
            <HashRouter>
            <div class="demo-layout-waterfall mdl-layout mdl-js-layout">
                <header class="mdl-layout__header mdl-layout__header--scroll mdl-color--primary">
                    <div class="mdl-layout--large-screen-only mdl-layout__header-row"/>
                    <div class="mdl-layout__tab-bar mdl-js-ripple-effect mdl-color--primary-dark">
                        <NavLink to="/" className="mdl-layout__tab">Home</NavLink>
                        <NavLink to="/tags" className="mdl-layout__tab">Tags</NavLink>
                        <NavLink to="/authors" className="mdl-layout__tab">Authors</NavLink>
                        <NavLink to="/news" className="mdl-layout__tab">News</NavLink>
                    </div>
                </header>
                <Route path="/tags" component={TagPage}/>
            </div>
            </HashRouter>
                
        )
    }
}