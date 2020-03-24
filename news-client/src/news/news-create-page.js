import React from 'react';
import $ from 'jquery'
import TagCheckList from './tag_check_list.js'

const NEWS_URL = 'http://localhost:8080/news-manager/news'

export default class NewsCreatePage extends React.Component {
    constructor() {
        super()
        this.news_tags_ref = React.createRef()
    }

    render() {
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
                                        id={"news-title-input-" + this.props.id} type="text"/>
                                
                            </div>
                            </div>
                        <div class="mdl-cell mdl-cell--1-col"></div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--8-col">
                        </div>
                        <div class="mdl-cell mdl-cell--3-col"></div>
                        
                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col">
                            <h5>Short text</h5>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--10-col">
                            <form action = "#">
                                <div class = "mdl-textfield mdl-js-textfield news_textfield">
                                    <textarea class = "news_textfield mdl-textfield__input" type = "text" rows =  "5" 
                                        id = "short_text_input"></textarea>
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
                                        id = "full_text_input"></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="mdl-cell mdl-cell--1-col"></div>

            
                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col">
                            <h4>Tags</h4>
                            <TagCheckList ref={this.news_tags_ref} selectedTags={[]}/>
                        </div>

                        <div class="mdl-cell mdl-cell--1-col"></div>
                        <div class="mdl-cell mdl-cell--11-col last">
                            <button onClick={this.create.bind(this)} class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored news-button">Create</button>
                        </div>
                        
                    </div>

                </div>
                <div class="mdl-cell mdl-cell--2-col">
                </div>
            </div>
        )
    
    }


    create() {
        let newTitle = $('#news-title-input-' + this.props.id).val()
        let newShortText = $('#short_text_input').val()
        let newFullText = $('#full_text_input').val()

        $.ajax(
            {
                method: 'POST',
                url: NEWS_URL,
                headers: { 
                    'Content-Type': 'application/json' 
                },
                dataType: 'json',
                data: JSON.stringify(
                    {
                        title: newTitle,
                        shortText: newShortText,
                        fullText: newFullText,
                        author: {
                            name: 'admin',
                            surname: 'admin'
                        },
                        tags: this.news_tags_ref.current.getSelected()
                    }
                )
            }
        ).fail((responce) => {
            alert('FAIL')
        }).done(responce => {
            window.location.href='/#/news'

        })
    }

}