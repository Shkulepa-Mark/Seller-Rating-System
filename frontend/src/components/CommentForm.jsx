import { useState } from 'react';
import { RATING_VALUES } from '../constants/comments';
import Alert from './Alert';
import FormField from './FormField';

const INITIAL_VALUES = { message: '', rating: '5' };

export default function CommentForm({ onSubmit, pending, error }) {
  const [values, setValues] = useState(INITIAL_VALUES);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const created = await onSubmit({ message: values.message.trim(), rating: Number(values.rating) });
    if (created !== undefined) {
      setValues(INITIAL_VALUES);
    }
  };

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <h3 className="card__title">Leave a review</h3>
      <FormField
        as="textarea"
        label="Your comment"
        name="message"
        rows="4"
        required
        value={values.message}
        onChange={handleChange}
      />
      <FormField as="select" label="Rating" name="rating" value={values.rating} onChange={handleChange}>
        {RATING_VALUES.map((value) => (
          <option key={value} value={value}>
            {value}
          </option>
        ))}
      </FormField>
      <Alert>{error?.message}</Alert>
      <button type="submit" className="btn" disabled={pending}>
        {pending ? 'Sending…' : 'Submit for review'}
      </button>
    </form>
  );
}
