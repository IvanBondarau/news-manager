import React from 'react'
import $ from 'jquery'
import i18n from '../i18n'

const AUTHOR_URL = 'http://localhost:8080/news-manager/author'

export default class NewsSearchBar extends React.Component {
    constructor() {
        super()
        this.state = {
            isUpdateNeeded: true,
            authors: []
        }
    }

    render() {
        if (this.state.isUpdateNeeded) {
            this.getData()
        }
        
        return (
            <div class="mdl-textfield mdl-js-textfield">
                <label htmlFor="author-select" class="mdl-textfield__label">{i18n.Author}</label>
                <select id="author-select" name="author-select" class="mdl-textfield__input">
                    <option></option>
                    {this.state.authors.map(
                        item => {
                            return (
                                <option value={item.name + ' ' + item.surname}>{item.name + ' ' + item.surname}</option>
                            )
                        }
                    )}
                </select>
            </div>
        )
    }

    getData() {
        $.ajax(
            {
                method: 'GET',
                url: AUTHOR_URL
            }
        ).fail(
            (responce) => {
                this.setState(
                    {
                        isLoaded: false,
                        authors: [],
                        isUpdateNeeded: false,
                        error: {
                            code: responce.statusCode,
                            text: responce.statusText,
                            comment: null
                        }
                    }
                )
            }
        ).done(
            (responce) => {
                this.setState(
                    {
                        authors: responce,
                        isUpdateNeeded: false,
                        isLoaded: true,
                        error: null
                    }
                )
            }
        )
    }

    getSelected() {
        let author = $('#author-select').children("option:selected").val();
        if (author === '') {
            return null
        } else {
            return {
                name: author.split(' ')[0],
                surname: author.split(' ')[1]
            }
        }
    }
}