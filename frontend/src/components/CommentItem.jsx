import { useState } from 'react';
import { RATING_VALUES } from '../constants/comments';
import { formatDate } from '../utils/format';
import Avatar from './Avatar';
import RatingBadge from './RatingBadge';

export default function CommentItem({ comment, canManage, onUpdate, onDelete }) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState({ message: comment.message, rating: String(comment.rating) });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setDraft((previous) => ({ ...previous, [name]: value }));
  };

  const handleSave = async () => {
    await onUpdate(comment.id, { message: draft.message.trim(), rating: Number(draft.rating) });
    setEditing(false);
  };

  const handleCancel = () => {
    setDraft({ message: comment.message, rating: String(comment.rating) });
    setEditing(false);
  };

  return (
    <article className="comment">
      <Avatar name={comment.authorName} size="sm" />
      <div className="comment__body">
        <header className="comment__header">
          <span className="comment__author">{comment.authorName}</span>
          <time className="comment__date" dateTime={comment.createdAt}>
            {formatDate(comment.createdAt)}
          </time>
          <RatingBadge value={comment.rating} size="sm" />
        </header>

        {editing ? (
          <>
            <textarea
              className="field__control"
              name="message"
              rows="3"
              aria-label="Comment text"
              value={draft.message}
              onChange={handleChange}
            />
            <select
              className="field__control comment__rating-select"
              name="rating"
              aria-label="Rating"
              value={draft.rating}
              onChange={handleChange}
            >
              {RATING_VALUES.map((value) => (
                <option key={value} value={value}>
                  {value}
                </option>
              ))}
            </select>
          </>
        ) : (
          <p className="comment__message">{comment.message}</p>
        )}

        {canManage && (
          <div className="comment__actions">
            {editing ? (
              <>
                <button type="button" className="btn btn--small" onClick={handleSave} disabled={!draft.message.trim()}>
                  Save
                </button>
                <button type="button" className="btn btn--small btn--ghost" onClick={handleCancel}>
                  Cancel
                </button>
              </>
            ) : (
              <>
                <button type="button" className="btn btn--small btn--ghost" onClick={() => setEditing(true)}>
                  Edit
                </button>
                <button type="button" className="btn btn--small btn--danger" onClick={() => onDelete(comment.id)}>
                  Delete
                </button>
              </>
            )}
          </div>
        )}
      </div>
    </article>
  );
}
