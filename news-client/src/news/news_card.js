import React from 'react';
import $ from 'jquery'
import { NavLink } from 'react-router-dom';

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsCard extends React.Component {

    constructor() {
        super()
        this.state = {}
    }

    render() {
        let date = new Date(this.props.item.creationDate); 
        return (
            <div class="news-card mdl-card mdl-shadow--2dp">
                <div class="mdl-card__title">
                    <h2 class="mdl-card__title-text">
                        {this.props.item.title}
                    </h2>
                </div>

                <div class="mdl-card__menu">
                    <span>
                        {date.toDateString()}
                    </span>
                </div>

                <div class="mdl-card__actions mdl-card--border">
                    <span>
                        {this.props.item.shortText}
                    </span>    
                </div>
                <div class="mdl-card__actions mdl-card--border">
                    <NavLink className="mdl-button mdl-js-button mdl-button--primary" to={'news/' + this.props.item.id}>Edit</NavLink>
                    <button class="mdl-button mdl-js-button mdl-button--primary">Delete</button>
                </div>
            </div>
        )
    
    }

    delete() {

        $.ajax(
            {
                method: 'DELETE',
                url: NEWS_URL + "/" + this.props.id
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            this.props.notifyAboutChanges()
        })
        
        
    }

    edit() {
        alert('Unsupported')
    }

}