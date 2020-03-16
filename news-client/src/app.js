import React from 'react'
import Header from './header.js'
import TagPage from './tag/tagpage.js'

export default class App extends React.Component {

    render() {
        return (
                    <div class="demo-layout-waterfall mdl-layout mdl-js-layout">
                        <Header/>
                        <TagPage/>
                    </div>
                
        )
    }
}