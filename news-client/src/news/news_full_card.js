import React from 'react';
import $ from 'jquery'
//import { NavLink, HashRouter } from 'react-router-dom';
import TagCheckList from './tag_check_list.js'

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsCard extends React.Component {
    constructor() {
        super()
        this.news_tags_ref = React.createRef()
    }

    render() {
        let creationDate = new Date(this.props.item.creationDate); 
        let modificationDate = new Date(this.props.item.modificationDate); 
        return (
            <div class="mdl-grid">
                <div class="mdl-cell mdl-cell--2-col">
                </div>
                <div class="mdl-cell mdl-cell--8-col">
                    <div class="mdl-grid mdl-grid--no-spacing news-full-card">


                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--10-col">
                            <div class="mdl-textfield mdl-js-textfield">
                                    <h4>Title</h4>
                                    <input class="mdl-textfield__input news-title-input" 
                                        placeholder={this.props.item.title} 
                                        id={"news-title-input-" + this.props.id} type="text"/>
                                
                            </div>
                            </div>
                        <div class="mdl-cell mdl-cell--1-col"></div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--8-col">
                            <h5>
                                {'Author: ' + this.props.item.author.name + ' ' + this.props.item.author.surname}
                            </h5>
                        </div>
                        <div class="mdl-cell mdl-cell--3-col">
                            <h5 class="news-full-card-title">{
                                'Created: ' + creationDate.toLocaleDateString()
                            }</h5>
                            <h5 class="news-full-card-title">{
                                'Last edited: ' + modificationDate.toLocaleDateString()
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
                        <div class="mdl-cell mdl-cell--11-col">
                            <h4>Tags</h4>
                            <TagCheckList ref={this.news_tags_ref} selectedTags={this.props.item.tags}/>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col last">
                            <button onClick={this.edit.bind(this)} class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button">Edit</button>
                            <button onClick={this.delete.bind(this)} class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button">Delete</button>
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
                url: NEWS_URL + "/" + this.props.item.id
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            this.props.notifyAboutChanges()
        })
        
        
    }

    edit() {
        let newTitle = $('#news-title-input-' + this.props.id).val()
        let newShortText = $('#short_text_input').val()
        let newFullText = $('#full_text_input').val()

        $.ajax(
            {
                method: 'PUT',
                url: NEWS_URL + "/" + this.props.item.id,
                headers: { 
                    'Content-Type': 'application/json' 
                },
                dataType: 'json',
                data: JSON.stringify(
                    {
                        id: this.props.item.id,
                        title: newTitle === '' ? this.props.item.title : newTitle,
                        shortText: newShortText === '' ? this.props.item.shortText : newShortText,
                        fullText: newFullText === '' ? this.props.item.fullText: newFullText,
                        creationDate: this.props.item.creationDate,
                        modificationDate: this.props.item.modificationDate,
                        author: this.props.item.author,
                        tags: this.news_tags_ref.current.getSelected()
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