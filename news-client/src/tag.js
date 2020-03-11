import React from 'react';
import $ from 'jquery'

const TAG_URL = 'http://localhost:8090/news-manager/tag'

export default class Tag extends React.Component {

    constructor() {
        super()
        this.state = {
            editable: true
        }
    }

    render() {
        if (this.state.editable) {
            return (
                <div class="tag-card mdl-card mdl-shadow--2dp">
                    <div class="mdl-card__title">
                        <h2 class="mdl-card__title-text">{this.props.name}</h2>
                    </div>
    
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                        onClick={this.delete.bind(this)}>
                        <i class="material-icons">delete</i>
                        </button>
                        <button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                        onClick={this.makeEditable.bind(this)}>
                        <i class="material-icons">edit</i>
                        </button>
                    </div>

                    <div class="mdl-card__actions mdl-card--border">
                        <form>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="sample1"/>
                                <label class="mdl-textfield__label" for="sample1">Enter new tag name</label>
                            </div>
                            <button type='submit' class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect">
                                <i class="material-icons">save</i>
                            </button>
                        </form>

                    </div>
                </div>
            )
        }
        return (
            <div class="tag-card mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">{this.props.name}</h2>
                </div>

                <div class="mdl-card__menu">
                    <button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                    onClick={this.delete.bind(this)}>
                    <i class="material-icons">delete</i>
                    </button>
                    <button class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                    onClick={this.makeEditable.bind(this)}>
                    <i class="material-icons">edit</i>
                    </button>
                </div>
            </div>
        )
    }

    delete() {

        $.ajax(
            {
                method: 'DELETE',
                url: TAG_URL + "/" + this.props.id
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            this.props.notifyAboutChanges()
        })
        
        
    }

    makeEditable() {
        this.setState( {
            editable: this.state.editable ? true : false
        })
    }
}