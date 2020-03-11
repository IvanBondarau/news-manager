import React from 'react'
import TagList from './taglist.js'
import Header from './header.js'

export default class App extends React.Component {

    render() {
        return (
            
            <div class="demo-layout-waterfall mdl-layout mdl-js-layout">
                <Header/>
                <div class='mdl-grid'>
                    <TagList/>
                </div>
            </div>
        )
    }
}