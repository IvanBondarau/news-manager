import React from 'react';
import $ from 'jquery'

const AUTHOR_URL = 'http://localhost:8080/news-manager/author'

export default class Author extends React.Component {

    constructor() {
        super()
        this.state = {}
    }

    render() {
        return (
            <div class="author-card mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">
                        {this.props.name + ' ' + this.props.surname} 
                    </h2>
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
                        <input class="mdl-textfield__input" placeholder={this.props.name + ' ' + this.props.surname} id={"author-name-input-" + this.props.id} type="text"/>
                    </div>
                </div>
            </div>
        )
    
    }

    delete() {

        $.ajax(
            {
                method: 'DELETE',
                url: AUTHOR_URL + "/" + this.props.id
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            this.props.notifyAboutChanges()
        })
        
        
    }

    edit() {
        var newName = $("#author-name-input-" + this.props.id).val().split(' ')
        $.ajax(
            {
                method: 'PUT',
                url: AUTHOR_URL + "/" + this.props.id,
                headers: { 
                    'Content-Type': 'application/json' 
                },
                dataType: 'json',
                data: JSON.stringify(
                    {
                        name: newName[0],
                        surname: newName[1]
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