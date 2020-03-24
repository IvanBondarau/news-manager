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
            
            <div class="mdl-grid" id='tag-page'>
                <div class="mdl-cell mdl-cell--4-col"/>
                <div class="mdl-cell mdl-cell--4-col">
                    <div class="mdl-card mdl-shadow--2dp">
                        <div class="mdl-card__title">
                            <h4 class="mdl-card__title-text">{this.props.item.title}</h4>
                        </div>
                        <div class="mdl-card__supporting-text mdl-card--border">
                            {date.toDateString()} 
                        </div>
                        <div class="mdl-card__supporting-text ">
                            <span>
                                {this.props.item.shortText}
                            </span>    
                        </div>
                        <div class="mdl-card__actions mdl-card--border">
                            <NavLink className="mdl-button mdl-js-button mdl-button--primary" to={'news/' + this.props.item.id}>Edit</NavLink>
                            <button onClick={this.delete.bind(this)} class="mdl-button mdl-js-button mdl-button--primary">Delete</button>
                        </div>
                    </div>
                </div>
                <div class="mdl-cell mdl-cell--4-col"/>
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


}