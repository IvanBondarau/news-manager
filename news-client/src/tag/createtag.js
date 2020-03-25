import React from 'react'
import $ from 'jquery'
import i18n from '../i18n'

const TAG_URL = 'http://localhost:8080/news-manager/tag'

export default class CreateTagForm extends React.Component {
    render() {
        return (
            <div class="create-tag-form mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">{i18n['Create new tag']}</h2>
                </div>
                <div class="mdl-card__actions mdl-card--border">
                    <div class="mdl-textfield mdl-js-textfield">
                        <input class="mdl-textfield__input" id="create-tag-form" type="text"/>
                    </div>
                </div>

                <div class="mdl-card__actions mdl-card--border">
                    <button onClick={this.handleCreate.bind(this)} class="mdl-button mdl-js-button mdl-button--primary">
                        {i18n.Create}
                    </button>
                </div>
            </div>
        )
    }

    handleCreate(event) {
        var newName = $("#create-tag-form").val()
        $.ajax(
            {
                method: 'POST',
                url: TAG_URL,
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
        }).done((responce) => {
            this.props.update()
        })
    }
}