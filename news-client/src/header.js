import React from 'react'



export default class Header extends React.Component {
    render() {
        return (
            <header class="mdl-layout__header mdl-layout__header--waterfall">

                <div class="mdl-layout__header-row">

                    <span class="mdl-layout-title">Welcome!</span>

                    <div class="mdl-layout-spacer"></div>
                    <div class="mdl-textfield mdl-js-textfield mdl-textfield--align-right">
                        Hello
                    </div>
                </div>
            </header>
        )
    }
}