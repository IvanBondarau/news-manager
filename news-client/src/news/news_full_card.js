import React from 'react';
import $ from 'jquery'
//import { NavLink, HashRouter } from 'react-router-dom';

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsCard extends React.Component {

    render() {
        let date = new Date(this.props.item.creationDate); 
        return (
            <div class="mdl-grid">
                <div class="mdl-cell mdl-cell--2-col">
                </div>
                <div class="mdl-cell mdl-cell--8-col">
                    <div class="mdl-grid mdl-grid--no-spacing news-full-card">
                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--10-col">
                            <h3 class="news-full-card-title">{this.props.item.title}</h3>
                        </div>
                        <div class="mdl-cell mdl-cell--1-col"></div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--8-col">
                            <h5 class="news-full-card-title">{
                                'Author: ' + this.props.item.author.name + ' ' + this.props.item.author.surname
                            }</h5>
                        </div>
                        <div class="mdl-cell mdl-cell--3-col">
                        <h5 class="news-full-card-title">{
                                'Creation date: ' + date.toLocaleDateString()
                            }</h5>
                        </div>
                        
                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col">
                            <h5>Short text</h5>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--10-col">
                            <form action = "#">
                                <div class = "mdl-textfield mdl-js-textfield news_textfield">
                                    <textarea class = "news_textfield mdl-textfield__input" type = "text" rows =  "5" 
                                        id = "short_text_input" placeholder={this.props.item.shortText}></textarea>
                                </div>
                            </form>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>

                        
                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col">
                            <h5>Full text</h5>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--10-col">
                            <form action = "#">
                                <div class = "mdl-textfield mdl-js-textfield news_textfield">
                                    <textarea class = "news_textfield mdl-textfield__input" type = "text" rows =  "5" 
                                        id = "full_text_input" placeholder={this.props.item.fullText}></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="mdl-cell mdl-cell--1-col"></div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col last">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button">Edit</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button">Delete</button>
                        </div>
                        
                    </div>

                </div>
                <div class="mdl-cell mdl-cell--2-col">
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