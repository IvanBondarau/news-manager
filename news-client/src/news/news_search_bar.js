import React from 'react'
import $ from 'jquery'

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
            <div class="mdl-selectfield mdl-js-selectfield">
                <select id="author-select" name="author-select" class="mdl-selectfield__select">
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
        return $('#author-select').children("option:selected").val();
    }
}