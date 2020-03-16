import React from 'react';
import $ from 'jquery'

const TAG_URL = 'http://localhost:8080/news-manager/tag'

export default class Tag extends React.Component {

    constructor() {
        super()
        this.state = {}
    }

    render() {
        return (
            <div class="tag-card mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">{this.props.name}</h2>
                </div>

                <div class="mdl-card__menu">
                    <button 
                        class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                        onClick={this.delete.bind(this)}>

                        <i class="material-icons">delete</i>
                    </button>

                    <button 
                        class="mdl-button mdl-button--icon mdl-js-button mdl-js-ripple-effect"
                        onClick={this.edit.bind(this)}>

                        <i class="material-icons">edit</i>
                    </button>
                </div>

                <div class="mdl-card__actions mdl-card--border">
                    <div class="mdl-textfield mdl-js-textfield">
                        <label for="sample1">Enter new tag name</label>
                        <input class="mdl-textfield__input" id={"tag-name-input-" + this.props.id} type="text"/>
                    </div>
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

    edit() {
        var newName = $("#tag-name-input-" + this.props.id).val()
        $.ajax(
            {
                method: 'PUT',
                url: TAG_URL + "/" + this.props.id,
                headers: { 
                    'Content-Type': 'application/json' 
                },
                dataType: 'json',
                data: JSON.stringify(
                    {
                        name: newName
                    }
                )
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            this.props.notifyAboutChanges()
        })
    }

}