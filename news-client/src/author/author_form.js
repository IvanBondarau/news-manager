import React from 'react'
import $ from 'jquery'

const AUTHOR_URL = 'http://localhost:8080/news-manager/author'

export default class CreateAuthorForm extends React.Component {
    render() {
        return (
            <div class="create-tag-form mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">Create new author</h2>
                </div>
                <div class="mdl-card__actions mdl-card--border">
                    <div class="mdl-textfield mdl-js-textfield">
                        <input class="mdl-textfield__input" id="create-author-form" type="text"/>
                    </div>
                </div>

                <div class="mdl-card__actions mdl-card--border">
                    <button onClick={this.handleCreate.bind(this)} class="mdl-button mdl-js-button mdl-button--primary">Submit</button>
                </div>
            </div>
        )
    }

    handleCreate(event) {
        var newName = $("#create-author-form").val().split(' ')
        $.ajax(
            {
                method: 'POST',
                url: AUTHOR_URL,
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
        }).done((responce) => {
            this.props.update()
        })
    }
}